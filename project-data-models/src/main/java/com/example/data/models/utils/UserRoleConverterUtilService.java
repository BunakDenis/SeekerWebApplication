package com.example.data.models.utils;


import com.example.data.models.enums.UserRoles;

public class UserRoleConverterUtilService {

    public static String convertMysticSchoolAccessesLevelToUserRole(byte access_level) {

        String result = "";

        switch (access_level) {

            case 0:
                result = UserRoles.TOURIST.getRole();
                break;

            case 1:
                result = UserRoles.USER.getRole();
                break;

            case 2:
                result = UserRoles.ADVANCED.getRole();
                break;

            case 3:
                result = UserRoles.ADMIN.getRole();
                break;
        }

        return result;

    }

}
