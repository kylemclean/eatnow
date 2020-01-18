package tech.eatnow;

import com.google.firebase.firestore.DocumentReference;

public class Food {
    String name;
    String description;
    String photo;
    DocumentReference provider;

    public Food() {}

    public Food(String name, String description, String photo, DocumentReference provider) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public DocumentReference getProvider() {
        return provider;
    }

    public void setProvider(DocumentReference provider) {
        this.provider = provider;
    }
}
