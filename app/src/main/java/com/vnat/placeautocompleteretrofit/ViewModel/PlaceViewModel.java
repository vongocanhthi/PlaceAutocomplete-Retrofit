package com.vnat.placeautocompleteretrofit.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vnat.placeautocompleteretrofit.API.ApiService;
import com.vnat.placeautocompleteretrofit.API.ClientService;
import com.vnat.placeautocompleteretrofit.Model.Place;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PlaceViewModel extends ViewModel {
    private ClientService clientService;
    private MutableLiveData<Place> placeMutableLiveData;

    public PlaceViewModel() {
        clientService = ApiService.getClientService();

        placeMutableLiveData = new MutableLiveData<>();

    }

    public void getPlace(String query, String key){
        clientService.getPlace(query, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Place>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Place place) {
                        placeMutableLiveData.setValue(place);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public MutableLiveData<Place> getPlaceMutableLiveData() {
        return placeMutableLiveData;
    }
}