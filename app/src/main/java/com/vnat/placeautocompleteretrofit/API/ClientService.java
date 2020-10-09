package com.vnat.placeautocompleteretrofit.API;

import com.vnat.placeautocompleteretrofit.Model.Place;

import io.reactivex.Single;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClientService {
    @POST("place/textsearch/json")
    Single<Place> getPlace(@Query("query") String query,
                           @Query("key") String key);

}
