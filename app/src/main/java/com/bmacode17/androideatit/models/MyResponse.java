package com.bmacode17.androideatit.models;

import java.util.List;

/**
 * Created by User on 14-Aug-18.
 */

public class MyResponse {

    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<MyResult> results;
}
