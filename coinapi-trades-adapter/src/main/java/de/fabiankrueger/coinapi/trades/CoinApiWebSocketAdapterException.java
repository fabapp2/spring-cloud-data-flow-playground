package de.fabiankrueger.coinapi.trades;

import java.io.IOException;

public class CoinApiWebSocketAdapterException extends RuntimeException {
    public CoinApiWebSocketAdapterException(Throwable e) {
        super(e);
    }
}
