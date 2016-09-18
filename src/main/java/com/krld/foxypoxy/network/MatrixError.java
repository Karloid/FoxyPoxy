package com.krld.foxypoxy.network;

public class MatrixError { //TODO rename

    private String errorMessage;

    public MatrixError(Throwable error) {
        errorMessage = error.getMessage();
    }

    public MatrixError() {
        //TODO
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
