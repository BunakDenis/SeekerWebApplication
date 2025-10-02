package com.example.data.models.enums;

public enum UserRoles {

    ADMIN ("admin", 3),
    ADVANCED("advanced", 2),
    USER ("user", 1),
    TOURIST ("tourist", 0);

    private final String role;
    private final int accessesLevel;

    UserRoles(String role, int accessesLevel) {
        this.role = role;
        this.accessesLevel = accessesLevel;
    }

    public String getRole() {
        return role;
    }

    public int getAccessesLevel() {
        return accessesLevel;
    }

    /**
     * Проверяет, имеет ли текущая роль доступ к уровню requiredRole.
     * @param requiredRole Минимально необходимая роль.
     * @return true, если доступ разрешен.
     */
    public boolean hasAccess(UserRoles requiredRole) {
        return this.accessesLevel >= requiredRole.accessesLevel;
    }

}
