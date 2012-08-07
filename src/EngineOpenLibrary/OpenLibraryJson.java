package EngineOpenLibrary;

import com.fasterxml.jackson.annotation.*;

public class OpenLibraryJson {

    private ISBN iSBN;

    @JsonAnySetter
    public void setUnknown(String key, ISBN value) {
        iSBN = value;
    }
    public ISBN getISBN()
    {
        return iSBN;
    }
}
