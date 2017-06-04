package icallable;

import impl.Main;

/**
 * Created by Vearthtek on 2017-06-04.
 */
@Description(description = "Konwersja z GBP na PLN")
public class GBPToPLN implements ICallable {
    static private Double ratio = Main.GETToRate("GBP");
    @Override
    public Double convert(Double d) {
        return d * ratio;
    }
}
