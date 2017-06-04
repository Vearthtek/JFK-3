package icallable;

import impl.Main;

/**
 * Created by Vearthtek on 2017-06-04.
 */
@Description(description = "Konwersja z PLN na USD")
public class PLNToUSD implements ICallable {
    static private Double ratio = Main.GETToRate("USD");
    @Override
    public Double convert(Double d) {
        return d / ratio;
    }
}
