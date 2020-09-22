package gms.cims.bridge;

import java.util.ArrayList;
import java.util.Arrays;

public class Arguments {
    public static String Broker ="localhost:29092";
    public static ArrayList<String> Topics = new ArrayList(Arrays.asList(
            "CIMSTEST.Customer.ClaimBlackList",
            "CIMSTEST.Financial.Claim",
            "CIMSTEST.Financial.ClaimCase",
            "CIMSTEST.Financial.ClaimContractLink",
            "CIMSTEST.Financial.ClaimCostPlus",
            "CIMSTEST.Financial.ClaimDrug",
            "CIMSTEST.Financial.ClaimEscAccumDetail",
            "CIMSTEST.Financial.ClaimNoteLink",
            "CIMSTEST.Financial.ClaimRefund",
            "CIMSTEST.Financial.ClaimStatus",
            "CIMSTEST.Financial.ClaimStatusClaimLink",
            "CIMSTEST.Financial.ClaimVoucherNoteClaimLink",
            "CIMSTEST.Reference.ClaimContractRelationships",
            "CIMSTEST.Reference.ClaimOverrideReason",
            "CIMSTEST.Reference.ClaimRefundReason",
            "CIMSTEST.Reference.ClaimState",
            "CIMSTEST.Reference.ClaimVoucherNote"
    ));
    public static String SchemaRegistry = "http://localhost:8081";
    public static String GroupId = "cimstest";
}
