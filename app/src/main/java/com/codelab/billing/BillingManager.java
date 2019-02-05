/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codelab.billing;

import android.content.Context;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * TODO: Implement BillingManager that will handle all the interactions with Play Store
 * (via Billing library), maintain connection to it through BillingClient and cache
 * temporary states/data if needed.
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String TAG = "BillingManager";
    public final BillingClient mBillingClient;
    private final Context mContext;

    public BillingManager(Context context) {
        mContext = context;
        mBillingClient = BillingClient.newBuilder(mContext).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) {
                if (billingResponse == BillingClient.BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() response: " + billingResponse);
                } else {
                    Log.w(TAG, "onBillingSetupFinished() error code: " + billingResponse);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });

    }

    @Override
    public void onPurchasesUpdated(@BillingClient.BillingResponse int responseCode,
                                   List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated() response: " + responseCode);
    }

    public void startPurchaseFlow(String skuId, String billingType) {
        // TODO: Implement launch billing flow here
    }


    private static final HashMap<String, List<String>> SKUS;

    static {
        SKUS = new HashMap<>();
        SKUS.put(BillingClient.SkuType.INAPP, Arrays.asList("gas", "premium"));
        SKUS.put(BillingClient.SkuType.SUBS, Arrays.asList("gold_monthly", "gold_yearly"));
    }

    public List<String> getSkus(@BillingClient.SkuType String type) {
        return SKUS.get(type);
    }

    public void querySkuDetailsAsync(@BillingClient.SkuType
                                     final String itemType,
                                     final List<String> skuList,
                                     final SkuDetailsResponseListener listener) {
        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(skuList).setType(itemType).build();
        mBillingClient.querySkuDetailsAsync(skuDetailsParams,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(
                            int responseCode,
                            List<SkuDetails> skuDetailsList) {
                        listener.onSkuDetailsResponse(responseCode,
                                skuDetailsList);
                    }
                });
    }


    public void getPurchaseHistory() {


        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {

                for (int i = 0; i < purchasesList.size(); i++) {
                    purchasesList.get(i).getPurchaseToken();
                }
            }
        });
    }
}
