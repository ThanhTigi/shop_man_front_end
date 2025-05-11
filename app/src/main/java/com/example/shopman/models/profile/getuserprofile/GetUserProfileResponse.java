package com.example.shopman.models.profile.getuserprofile;

public class GetUserProfileResponse {
    private String messsage;
    private int status;

    private GetUserProfileMetaData metadata;

    public GetUserProfileMetaData getUserProfileMetaData()
    {
        return metadata;
    }

}
