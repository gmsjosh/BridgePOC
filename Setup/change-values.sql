use CIMS
UPDATE CIMS.Customer.ClaimBlackList SET IsIndividual=(CASE IsIndividual WHEN 1 THEN 0 ELSE 1 END) WHERE CO_ContractID=407
UPDATE CIMS.Financial.ClaimCostPlus SET CCP_AdminFee=(CASE CCP_AdminFee WHEN 49.71 THEN 50.00 ELSE 49.71 END) WHERE CL_ClaimID=16937635
select * from CIMS.Customer.ClaimBlackList WHERE CO_ContractID=407
select * from CIMS.Financial.ClaimCostPlus WHERE CL_ClaimID=16937635

select * from Financial.ClaimVoucherNoteClaimLink
select * from Financial.ClaimNoteLink