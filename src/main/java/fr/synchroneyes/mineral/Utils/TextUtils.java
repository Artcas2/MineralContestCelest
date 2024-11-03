package fr.synchroneyes.mineral.Utils;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.text.WordUtils;

public class TextUtils {
    public static List<String> textToLore(String text, int nbCaractereAvantNouvelleLigne) {
        String wrapped = WordUtils.wrap(text, nbCaractereAvantNouvelleLigne);
        wrapped = wrapped.replace("\r", "");
        return Arrays.asList(wrapped.split("\n"));
    }
}

