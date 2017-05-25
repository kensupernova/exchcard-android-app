package com.guanghuiz.exchangecard.SendReceiveApi;

import com.guanghuiz.exchangecard.Database.model.AvatarPhotoResponse;
import com.guanghuiz.exchangecard.Database.model.Card;
import com.guanghuiz.exchangecard.Database.model.CardCount;
import com.guanghuiz.exchangecard.Database.model.NewsFeed;
import com.guanghuiz.exchangecard.Database.model.RecipientForCard;
import com.guanghuiz.exchangecard.Database.model.AddCardResponse;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressRequestData;
import com.guanghuiz.exchangecard.Database.model.RegisterUserAddressResponseData;
import com.guanghuiz.exchangecard.Database.model.MailAddress;
import com.guanghuiz.exchangecard.Database.model.ServerAuthResponse;
import com.guanghuiz.exchangecard.Database.model.User;
import com.guanghuiz.exchangecard.Database.model.ReceiveCardWithPhotoResponse;
import com.guanghuiz.exchangecard.Database.model.CardPhoto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Guanghui on 17/2/16.
 */
public interface ServiceApi {
    String ENDPOINT =
                "http://exchcard.applinzi.com/api/exchcard/";

    String USER_ADDRESS_REGISTER_SVC_PATH =  "register/";
    String USER_AUTH_SVC_PATH =  "auth/";
    String USER_REGISTER_PATH =  "users/register/";

    String PROFILE_PATH = "profiles/";
    String PROFILE_PATH_ID = "profiles/{id}/";
    String PROFILE_PATH_ID_ICON= "profiles/{id}/icon/";
    String PROFILE_PATH_ID_ICON_DATA= "profiles/{id}/icon/data/";
    String PROFILE_GETONE = "profiles/getone/";
    String PROFILE_ID_CARDS = "profiles/{pk}/getcards/";

    String PROFILE_ID_AVATARF = "profiles/{pk}/avatarf/";
    String PROFILE_ID_AVATAR = "profiles/{pk}/avatar/";

    String ADDRESS_REGISTER = "address/register/";
    String ADDRESS_UPDATE = "address/update/{id}/";
    String ADDRESS_GETONE = "address/getone/";
    String ADDRESS_GETONE_ID =  ADDRESS_GETONE + "{id}/";


    String CARD_ADD =  "cards/add/";
    String CARD_REGISTER_RECEIVE =  "cards/receive/";
    String CARD_REGISTER_RECEIVE_with_PHOTO = "cards/receivewithphoto/";
    String CARD_ADD_CARDPHOTO =  "cards/{pk}/cardphoto";

    String NEWSFEED ="newsfeed/";

    @GET(ADDRESS_GETONE)
    Call<MailAddress> getMailAddress(@Header("Authorization") String authorization);

    @GET(ADDRESS_GETONE)
    Call<MailAddress> getMailAddress();

    @GET(PROFILE_GETONE)
    Call<RecipientForCard> getRecipientForCard();

    @GET(ADDRESS_GETONE_ID)
    Call<MailAddress> getMailAddress(@Path("userid") long userid);

    @POST(ADDRESS_REGISTER)
    Call<MailAddress> addAddress(@Body MailAddress mailAddress);

    @PUT(ADDRESS_UPDATE)
    Call<MailAddress> updateAddress(@Body MailAddress mailAddress, @Path("id") int profile_id);

    @FormUrlEncoded
    @POST(ADDRESS_REGISTER)
    Call<MailAddress> addAddressAsForm(@Field("name") String name,
                                              @Field("address") String address,
                                              @Field("postcode") String postcode);


    @Multipart
    @POST(PROFILE_PATH_ID_ICON_DATA)
    Call<Boolean> addProfileIconDataWithID(@Part("photo") RequestBody photo,
                                              @Part("description") RequestBody description);

    @FormUrlEncoded
    @POST(USER_REGISTER_PATH)
    Call<RegisterUserAddressResponseData> registerUserAsForm(@Field("registerinfo") String information);


    @POST(USER_ADDRESS_REGISTER_SVC_PATH)
    Call<RegisterUserAddressResponseData> registerUserAddressProfile(@Body RegisterUserAddressRequestData info);

    @POST(USER_AUTH_SVC_PATH)
    Call<ServerAuthResponse> authUser(@Body User user);

    @POST(CARD_ADD)
    Call<AddCardResponse> addCard(@Body Card card);

    @GET(PROFILE_ID_CARDS)
    Call<CardCount> getCardCount(@Path("pk") int pk);

    @FormUrlEncoded
    @PUT(CARD_REGISTER_RECEIVE)
    Call<Card> registerReceivedCard(@Field("card_name") String card_name);

    @Multipart
    @POST(CARD_REGISTER_RECEIVE_with_PHOTO)
    Call<CardPhoto> registerReceivedCardWithPhoto(@Field("card_name") String card_name,
                                                  @Part MultipartBody.Part cardphoto,
                                                  @Part("title") RequestBody title);

    @Multipart
    @POST(CARD_ADD_CARDPHOTO)
    Call<ReceiveCardWithPhotoResponse> addCardPhoto(@Part MultipartBody.Part cardphoto,
                                                    @Path("pk") int card_id );

    @Multipart
    @POST(PROFILE_ID_AVATAR)
    Call<AvatarPhotoResponse> updateAvatarPhoto(@Part MultipartBody.Part avatar,
                                                    @Path("pk") int profile_id,
                                                    @Part("description") RequestBody description);
    @Multipart
    @POST(PROFILE_ID_AVATAR)
    Call<AvatarPhotoResponse> updateAvatarPhoto2(@Part MultipartBody.Part avatar,
                                                 @Path("pk") int profile_id );

    @Multipart
    @POST("http://exchangecard2.applinzi.com/")
    Call<CardPhoto> addCardPhoto(@Part MultipartBody.Part cardphoto,
                                 @Part("title") RequestBody title);

    @GET(NEWSFEED)
    Call<NewsFeed> get_newsfeed(@Field("pk") int profile_id);

}
