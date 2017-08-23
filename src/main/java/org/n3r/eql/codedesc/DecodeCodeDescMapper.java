package org.n3r.eql.codedesc;

import com.google.common.base.Splitter;
import lombok.val;

public class DecodeCodeDescMapper extends DefaultCodeDescMapper {
    public DecodeCodeDescMapper(String valuesStr) {
        val values = Splitter.on(',').trimResults().splitToList(valuesStr);

        int i = 0, ii = values.size();
        for (; i + 1 < ii; i += 2) {
            addMapping(values.get(i), values.get(i + 1));
        }

        if (i < ii) defaultDesc = values.get(ii - 1);
    }
}
