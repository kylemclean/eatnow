package tech.eatnow;

import com.google.common.base.Objects;
import com.google.firebase.firestore.GeoPoint;

public class Provider {

    String name;
    String address;
    GeoPoint location;

    public Provider() {
    }

    public Provider(String name, String address, GeoPoint location) {
        this.name = name;
        this.address = address;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
                Objects.equal(address, provider.address) &&
                Objects.equal(location, provider.location);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, address, location);
    }
}
