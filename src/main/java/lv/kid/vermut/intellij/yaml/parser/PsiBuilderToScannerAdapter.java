package lv.kid.vermut.intellij.yaml.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.util.text.CharSequenceReader;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerEx;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.Token;

/**
 * Created by Pavels.Veretennikovs on 2015.06.27..
 */
public class PsiBuilderToScannerAdapter implements ScannerEx {
    private final PsiBuilder builder;
    private final Scanner scanner;
    private final StreamReader streamReader;
    private boolean peekMode;
    private int builderTokensBehind = 0;

    public PsiBuilderToScannerAdapter(PsiBuilder builder) {
        this.builder = builder;
        streamReader = new StreamReader(new CharSequenceReader(builder.getOriginalText()));
        scanner = new ScannerImpl(streamReader);
    }

    @Override
    public boolean checkToken(Token.ID... choices) {
        return scanner.checkToken(choices);

/*
        try {
            return scanner.checkToken(choices);
       } catch (ScannerException e) {
            builder.error(e.getMessage());
            return false;
        }
*/
    }

    @Override
    public Token peekToken() {
        return scanner.peekToken();
    }

    @Override
    public Token getToken() {
        builderTokensBehind++;

        if (!isPeekMode()) {
            catchUpWithScanner();
        }
        return scanner.getToken();
    }

    @Override
    public void catchUpWithScanner() {
        while (builderTokensBehind > 0) {
            builder.advanceLexer();
            builderTokensBehind--;
        }
    }

    public boolean isPeekMode() {
        return peekMode;
    }

    @Override
    public void setPeekMode(boolean peekMode) {
        this.peekMode = peekMode;
    }

    @Override
    public PsiBuilder.Marker getMarker() {
        return builder.mark();
    }

    @Override
    public void markError(String error) {
        builder.error(error);
    }
}
