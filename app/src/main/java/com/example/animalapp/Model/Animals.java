//package com.example.animalapp.Model;
//
//import android.widget.RadioButton;
//
//public class Animals {
//    private String name;
//    private String id;
//    private String image;
//    private String owner;
//    private String animalType;
//    private Integer age;
//    private RadioButton b1;
//    private RadioButton b2;
//    private RadioButton b3;
//    private RadioButton b4;
//    private RadioButton b5;
//    private RadioButton b6;
//
//    public Animals() {
//        // Default constructor
//    }
//
//    // Getter and Setter methods for each field
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public String getOwner() {
//        return owner;
//    }
//
//    public void setOwner(String owner) {
//        this.owner = owner;
//    }
//    public String getAnimalType(){
//        return animalType;
//    }
//    public void setAnimalType(String animalType){
//        this.animalType = animalType;
//    }
//
//    public Integer getAge() {
//        return age;
//    }
//
//    public void setAge(Integer age) {
//        this.age = age;
//    }
//
////    public RadioButton getB1() {
////        return b1;
////    }
////
////    public void setB1(RadioButton b1) {
////        this.b1 = b1;
////    }
////
////    public RadioButton getB2() {
////        return b2;
////    }
////
////    public void setB2(RadioButton b2) {
////        this.b2 = b2;
////    }
////
////    public RadioButton getB3() {
////        return b3;
////    }
////
////    public void setB3(RadioButton b3) {
////        this.b3 = b3;
////    }
////
////    public RadioButton getB4() {
////        return b4;
////    }
////
////    public void setB4(RadioButton b4) {
////        this.b4 = b4;
////    }
////
////    public RadioButton getB5() {
////        return b5;
////    }
////
////    public void setB5(RadioButton b5) {
////        this.b5 = b5;
////    }
////
////    public RadioButton getB6() {
////        return b6;
////    }
////
////    public void setB6(RadioButton b6) {
////        this.b6 = b6;
////    }
//}
//

package com.example.animalapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Animals implements Parcelable {
    private String id;
    private String image;
    private String name;
    private String age;
    private String owner;
    private String animalType;
    private String gender;
    private String description;

    public Animals() {
        // Default constructor required for calls to DataSnapshot.getValue(Animals.class)
    }

    public Animals(String id, String image, String name, String age, String owner, String animalType, String gender, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.age = age;
        this.owner = owner;
        this.animalType = animalType;
        this.gender = gender;
        this.description = description;
    }

    protected Animals(Parcel in) {
        id = in.readString();
        image = in.readString();
        name = in.readString();
        age = in.readString();
        owner = in.readString();
        animalType = in.readString();
        gender = in.readString();
        description = in.readString();
    }

    public static final Creator<Animals> CREATOR = new Creator<Animals>() {
        @Override
        public Animals createFromParcel(Parcel in) {
            return new Animals(in);
        }

        @Override
        public Animals[] newArray(int size) {
            return new Animals[size];
        }
    };

    public String getGender() {return gender;}

    public void setGender(String gender) {this.gender = gender;}

    public String getId() {
        return id;
    }
    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String  getAge() {
        return age;
    }

    public String getOwner() {
        return owner;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getDescription(){return description;}

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }
    public void setDescription(String description){this.description = description;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(age);
        dest.writeString(owner);
        dest.writeString(animalType);
        dest.writeString(gender);
        dest.writeString(description);
    }
}
