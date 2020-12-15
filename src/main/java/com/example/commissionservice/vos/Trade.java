package com.example.commissionservice.vos;

import com.example.commissionservice.exceptions.InvalidFieldException;
import lombok.Builder;
import lombok.Data;

import static com.example.commissionservice.utils.Constants.*;

@Builder
@Data
public class Trade {
    String tradeId;
    String counterParty,product,exchange;
    Double tradeValue;

    public String getFieldValue(RuleLookUpFields ruleLookUpFields) {
        if(ruleLookUpFields.equals(RuleLookUpFields.CounterPartyId))
            return counterParty==null?"":counterParty.trim();
        else if(ruleLookUpFields.equals(RuleLookUpFields.Product))
            return product==null?"":product.trim();
        else if(ruleLookUpFields.equals(RuleLookUpFields.TradeValue))
            return tradeValue==null?"":tradeValue.toString();
        else if (ruleLookUpFields.equals(RuleLookUpFields.Exchange))
            return exchange==null?"":exchange.trim();
        else throw new InvalidFieldException("Unknown Field to for extraction");
    }
}
