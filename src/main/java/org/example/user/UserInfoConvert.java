package org.example.user;

import com.google.gson.reflect.TypeToken;
import org.example.conf.BaseConfiguration;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;

public class UserInfoConvert implements AttributeConverter<UserInfo, String> {
    @Override
    public String convertToDatabaseColumn(UserInfo attribute) {
        return BaseConfiguration.generalGson().toJson(attribute);
    }

    @Override
    public UserInfo convertToEntityAttribute(String dbData) {
        return BaseConfiguration.generalGson().fromJson(dbData, new TypeToken<UserInfo>(){}.getType());
    }
}
