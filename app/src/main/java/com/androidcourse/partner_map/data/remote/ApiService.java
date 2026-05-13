package com.androidcourse.partner_map.data.remote;

import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.model.Evaluation;
import com.androidcourse.partner_map.model.Participation;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.model.School;
import com.androidcourse.partner_map.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body Map<String, Object> body);

    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body Map<String, Object> body);

    @GET("auth/me")
    Call<ApiResponse<User>> getCurrentUser();

    @GET("users/{userId}")
    Call<ApiResponse<User>> getUser(@Path("userId") String userId);

    @GET("schools")
    Call<ApiResponse<List<School>>> getSchools();

    @GET("requests")
    Call<ApiResponse<PaginatedData<PartnerRequest>>> getRequests(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radius") int radius,
            @Query("category") Integer category,
            @Query("schoolId") String schoolId,
            @Query("timeFilter") String timeFilter,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("requests")
    Call<ApiResponse<PartnerRequest>> createRequest(@Body PartnerRequest request);

    @PUT("requests/{requestId}")
    Call<ApiResponse<PartnerRequest>> updateRequest(@Path("requestId") String requestId, @Body PartnerRequest request);

    @GET("requests/{requestId}")
    Call<ApiResponse<PartnerRequest>> getRequestDetail(@Path("requestId") String requestId);

    @POST("requests/{requestId}/participate")
    Call<ApiResponse<java.util.Map<String, Object>>> participate(@Path("requestId") String requestId);

    @POST("requests/{requestId}/cancel")
    Call<ApiResponse<Void>> cancelRequest(@Path("requestId") String requestId);

    @POST("requests/{requestId}/complete")
    Call<ApiResponse<Void>> completeRequest(@Path("requestId") String requestId);

    @GET("requests/my")
    Call<ApiResponse<PaginatedData<PartnerRequest>>> getMyRequests();

    @GET("chat/rooms")
    Call<ApiResponse<List<ChatRoom>>> getChatRooms();

    @POST("chat/rooms")
    Call<ApiResponse<ChatRoom>> createChatRoom(@Body Map<String, String> body);

    @GET("chat/rooms/{roomId}/messages")
    Call<ApiResponse<PaginatedData<ChatMessage>>> getMessages(@Path("roomId") String roomId);

    @POST("chat/rooms/{roomId}/messages")
    Call<ApiResponse<ChatMessage>> sendMessage(@Path("roomId") String roomId, @Body Map<String, String> body);

    @POST("participations/{participationId}/approve")
    Call<ApiResponse<Void>> approveParticipation(@Path("participationId") String participationId);

    @POST("participations/{participationId}/reject")
    Call<ApiResponse<Void>> rejectParticipation(@Path("participationId") String participationId);

    @GET("participations/my")
    Call<ApiResponse<PaginatedData<Participation>>> getMyParticipations();

    @GET("evaluations/pending")
    Call<ApiResponse<PaginatedData<Evaluation>>> getPendingEvaluations();

    @POST("evaluations")
    Call<ApiResponse<Void>> submitEvaluation(@Body Evaluation evaluation);
}
