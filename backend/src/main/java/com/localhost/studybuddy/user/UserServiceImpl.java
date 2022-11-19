package com.localhost.studybuddy.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhost.studybuddy.geocoding.ReverseGeocodingResponse;
import com.localhost.studybuddy.role.Role;
import com.localhost.studybuddy.role.RoleService;
import com.localhost.studybuddy.util.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;

import static com.localhost.studybuddy.role.RoleServiceImpl.DEFAULT_ROLE;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserGeoLocationService userGeoLocationService;

    public static final String UPLOAD_DIR = "user_uploads/";
    public static final String UPLOAD_DIR_PATH = "src/main/resources/static/user_uploads/";
    public String GEOCODING_URL = "http://api.positionstack.com/v1/reverse?access_key=7b72fe2dd3ccd9963408ba1493167bec&query=";
    private static final String LIMIT = "&limit=1";



    @Override
    public UserModel createUser(UserDto userDto) {
        if(checkIfUserAlreadyExist(userDto.getEmail()))
            throw new RuntimeException("User with email  " + userDto.getEmail() + " already exists");

        UserModel user = UserModel.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .lastname(userDto.getLastname())
                .imagePath(UPLOAD_DIR + "default_img.png")
                .gender(userDto.getGender())
                .birthdate(userDto.getBirthdate())
                .university(userDto.getUniversity())
                .build();

        addDefaultRole(user);
        return userRepository.save(user);
    }




    public Page<UserModel> findAllBasedOnCriteria(UserModel userModel, Pageable pageable){
        ExampleMatcher matcher = configureMatcher();

        Example<UserModel> probe = Example.of(userModel, matcher);

        return userRepository.findAll(probe,pageable);
    }


    public Specification<UserModel> getSpecFromDatesAndExample(Date from, Date to, Example<UserModel> example){
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if(from != null)
                predicates.add(builder.greaterThanOrEqualTo(root.get("birthdate"),from));

            if(to != null)
                predicates.add(builder.lessThanOrEqualTo(root.get("birthdate"), to));

            predicates.add(QueryByExamplePredicateBuilder.getPredicate(root,builder,example));

            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public Page<UserModel> findAllByConditions(UserModel userModel, Pageable pageable, int ageGroup){
        ExampleMatcher matcher = configureMatcher();

        Example<UserModel> example = Example.of(userModel,matcher);

        if (ageGroup != 0) {
            Date[] dates = getDatesFromAgeGroup(ageGroup);
            return userRepository.findAll(getSpecFromDatesAndExample(dates[0],dates[1],example),pageable);
        }else{
            return findAllBasedOnCriteria(userModel,pageable);
        }

    }


    public String doReverseGeocoding(GeoLocation geoLocation){
        RestTemplate restTemplate = new RestTemplate();
        String latitude  = geoLocation.getLatitude();
        String longitude = geoLocation.getLongitude();
        ObjectMapper mapper = new ObjectMapper();

        String result = restTemplate.getForObject(GEOCODING_URL + latitude + "," + longitude + LIMIT,String.class);
        try{
            ReverseGeocodingResponse  results = mapper.readValue(result, ReverseGeocodingResponse.class);
            return results.getData()[0].getLabel();
        }catch (Exception e){
            throw new RuntimeException("Error while doing reverse geocoding");
        }

    }




    public List<UserFilteredResponse> findAllUsersWithFiltersApplied(UserModel userModel, Pageable pageable, int ageGroup, GeoLocation geoLocation,int distance){
        Page<UserModel> allByConditions = findAllByConditions(userModel, pageable, ageGroup);
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> geoResults = userGeoLocationService.searchForUsers(geoLocation,distance);
        System.out.println(geoResults);

        List<Integer> ids = new ArrayList<>();
        List<UserFilteredResponse> users = new ArrayList<>();

        geoResults.forEach(x-> ids.add(Integer.valueOf(x.getContent().getName())));
        Collections.sort(ids);
        System.out.println(allByConditions.getContent().size() + "Size of db query");
        System.out.println("ids size" + ids.size());

        for(UserModel user : allByConditions){
            int i = Collections.binarySearch(ids, user.getId());
            if ( i > -1){
                UserFilteredResponse userFilteredResponse = parseUserModel(user);
                Point point = geoResults.get(i).getContent().getPoint();

                userFilteredResponse.setCurrentStudyBuddyAddress(doReverseGeocoding(new GeoLocation(point)));
                users.add(userFilteredResponse);
            };
        }
        System.out.println(users.size() + "Size");
        return users;
    }

    private Date[] getDatesFromAgeGroup(int ageGroup){
        int year = Year.now().getValue();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            switch (ageGroup) {
                case 1:
                    return new Date[]{format.parse("01-01-" + (year - 20)), format.parse("01-01-" + (year-18))};
                case 2:
                    return new Date[]{format.parse("01-01-" + (year - 22)), format.parse("01-01-" + (year-20))};
                case 3:
                    return new Date[]{format.parse("01-01-" + (year - 24)), format.parse("01-01-" + (year-22))};
                case 4:
                    return new Date[]{format.parse("01-01-" + (year - 26)), format.parse("01-01-" + (year-24))};
                default:
                    return new Date[]{format.parse("01-01-" + (year - 50)), format.parse("01-01-" + (year))};
            }

        }catch (Exception e){
            throw new RuntimeException("error while parsing dates");
        }
    }

    @Override
    public UserModel getAuthenticatedUser() {
        return userRepository.findUserModelByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()-> new RuntimeException("Error retrieving user from security context"));
    }

    @Override
    @Transactional
    public void updateUserImage(MultipartFile image) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userRepository.updateUserImage(auth.getName(),UPLOAD_DIR + auth.getName() + ".png");
        uploadUserImage(auth.getName(),image);
    }

    private void uploadUserImage(String user, MultipartFile image){
        try {
            File file = new File(UPLOAD_DIR_PATH + user + ".png");
            BufferedImage img = ImageIO.read(image.getInputStream());
            ImageIO.write(img, "png", file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private void addDefaultRole(UserModel user){
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findRoleByName(DEFAULT_ROLE));
        user.setRoles(roles);
    }

    private UserFilteredResponse parseUserModel(UserModel userModel){
        return UserFilteredResponse.builder()
                .email(userModel.getEmail())
                .imagePath(userModel.getImagePath())
                .imagePath(userModel.getImagePath())
                .name(userModel.getName())
                .gender(userModel.getGender())
                .id(userModel.getId())
                .lastName(userModel.getLastname())
                .birthdate(userModel.getBirthdate())
                .university(userModel.getUniversity()).build();
    }

    private ExampleMatcher configureMatcher(){
        return ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();
    }



    public void initUsers(){
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            UserModel user = createInitUser(UserDto.builder().name("Vladimir").gender(false).lastname("Petkovic").university("RAF").email("jstojic@gmail.com").password("123").birthdate(format.parse("01-01-2000")).gender(false).build());
            UserModel user1 = createInitUser(UserDto.builder().name("Marina").gender(false).lastname("Solic").university("ETF").email("marinasolic@gmail.com").password("123").birthdate(format.parse("01-01-2002")).gender(true).build());
            UserModel user2 = createInitUser(UserDto.builder().name("Milovan").gender(false).lastname("Glisic").university("MATF").email("milovanglisic@gmail.com").password("123").birthdate(format.parse("01-01-2003")).gender(false).build());
            UserModel user3 = createInitUser(UserDto.builder().name("Vasko").gender(false).lastname("Popa").university("PMF").email("vaskopopa@gmail.com").password("123").birthdate(format.parse("01-01-1998")).gender(false).build());
            UserModel user4 = createInitUser(UserDto.builder().name("Jovan").gender(false).lastname("Popovic").university("RAF").email("jovanpopic@gmail.com").password("123").birthdate(format.parse("01-01-1999")).gender(false).build());


            userGeoLocationService.addStudyBuddy(new GeoLocation("43.8888068298014","20.337533526592104"), user.getId());
            userGeoLocationService.addStudyBuddy(new GeoLocation("43.89016068660577","20.333778618014403"), user1.getId());
            userGeoLocationService.addStudyBuddy(new GeoLocation("43.8888068298014","20.337533526592104"), user2.getId());
            userGeoLocationService.addStudyBuddy(new GeoLocation("43.891188107823815","20.337890777957252"), user3.getId());
            userGeoLocationService.addStudyBuddy(new GeoLocation("43.89170172637468","20.339782753919764"), user4.getId());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private UserModel createInitUser(UserDto userDto){
        if (checkIfUserAlreadyExist(userDto.getEmail())){
            return userRepository.findUserModelByEmail(userDto.getEmail()).orElseThrow(()-> new RuntimeException("Not found"));
        }
        UserModel user = UserModel.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .lastname(userDto.getLastname())
                .imagePath(UPLOAD_DIR + "default_img.png")
                .gender(userDto.getGender())
                .birthdate(userDto.getBirthdate())
                .university(userDto.getUniversity())
                .build();

        addDefaultRole(user);
        return userRepository.save(user);

    }




    private boolean checkIfUserAlreadyExist(String email){
        return userRepository.existsByEmail(email);
    }


}
