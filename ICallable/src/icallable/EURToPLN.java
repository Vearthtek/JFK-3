package icallable;

import impl.Main;

/**
 * Created by Vearthtek on 2017-06-04.
 */
@Description(description = "Konwersja z EUR na PLN")
public class EURToPLN implements  ICallable {
    static private Double ratio = Main.GETToRate("EUR");
    @Override
    public Double convert(Double d) {
        return d * ratio;
    }
}
