package org.example.orafucharles.e_school;

public class SchoolAdmin {
    private int id;
    private String  email, name, external_table_id, email_verified_at, active, user_category, created_at, updated_at, deleted_at;

    public SchoolAdmin(int id, String email, String name, String external_table_id, String email_verified_at, String active, String user_category, String created_at, String updated_at, String deleted_at) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.external_table_id = external_table_id;
        this.email_verified_at = email_verified_at;
        this.active = active;
        this.user_category = user_category;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }


    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getExternal_table_id() {
        return external_table_id;
    }

    public String getEmail_verifed_at() {
        return email_verified_at;
    }

    public String getActive() {
        return active;
    }

    public String getUser_category() {
        return user_category;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }
}
