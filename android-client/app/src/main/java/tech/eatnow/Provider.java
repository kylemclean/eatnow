package tech.eatnow;

import com.google.common.base.Objects;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

public class Provider {

    String name;
    GeoPoint location;

    public Provider() {
    }

    public Provider(String name, GeoPoint location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider provider = (Provider) o;
        return Objects.equal(name, provider.name) &&
                Objects.equal(location, provider.location);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, location);
    }
}
