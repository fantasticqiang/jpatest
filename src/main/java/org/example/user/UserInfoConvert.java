package org.example.user;

import com.google.gson.reflect.TypeToken;
import org.example.conf.BaseConfiguration;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;

public class UserInfoConvert implements AttributeConverter<ArrayList<UserInfo>, String> {
    @Override
    public String convertToDatabaseColumn(ArrayList<UserInfo> attribute) {
        return BaseConfiguration.generalGson().toJson(attribute);
    }

    @Override
    public ArrayList<UserInfo> convertToEntityAttribute(String dbData) {
        return BaseConfiguration.generalGson().fromJson(dbData, new TypeToken<ArrayList<UserInfo>>(){}.getType());
    }
}
