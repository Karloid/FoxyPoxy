package com.krld.foxypoxy.responses;

import java.util.List;

public class BaseResponse<T> {
    public Boolean ok;
    public List<T> result;
}
