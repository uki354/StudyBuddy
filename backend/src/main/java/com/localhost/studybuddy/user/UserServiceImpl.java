package com.localhost.studybuddy.user;

import com.localhost.studybuddy.role.Role;
import com.localhost.studybuddy.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.persistence.criteria.Predicate;
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



    @Override
    public void createUser(UserDto userDto) {
        if(checkIfUserAlreadyExist(userDto.getEmail()))
            throw new RuntimeException("User with email  " + userDto.getEmail() + " already exists");

        UserModel user = UserModel.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .lastname(userDto.getLastname())
                .build();

        addDefaultRole(user);
        userRepository.save(user);
    }


    public Page<UserModel> findAllBasedOnCriteria(UserModel userModel, Pageable pageable){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                .withIgnoreCase()
                .withIgnoreNullValues();

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
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnoreNullValues();

        Example<UserModel> example = Example.of(userModel,matcher);

        if (ageGroup != 0) {
            Date[] dates = getDatesFromAgeGroup(ageGroup);
            System.out.println(Arrays.toString(dates));
            return userRepository.findAll(getSpecFromDatesAndExample(dates[0],dates[1],example),pageable);
        }else{
            return findAllBasedOnCriteria(userModel,pageable);
        }




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



    private void addDefaultRole(UserModel user){
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findRoleByName(DEFAULT_ROLE));
        user.setRoles(roles);
    }

    private boolean checkIfUserAlreadyExist(String email){
        return userRepository.existsByEmail(email);
    }


}
