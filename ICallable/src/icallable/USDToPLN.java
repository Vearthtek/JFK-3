package icallable;

import impl.Main;

/**
 * Created by Vearthtek on 2017-06-04.
 */
@Description(description = "Konwersja z USD na PLN")
public class USDToPLN implements ICallable {
    static private Double ratio = Main.GETToRate("USD");
    @Override
    public Double convert(Double d) {
        return d * ratio;
    }
}
