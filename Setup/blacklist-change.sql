use CIMS
UPDATE CIMS.Customer.ClaimBlackList SET IsIndividual=(CASE IsIndividual WHEN 1 THEN 0 ELSE 1 END) WHERE CO_ContractID=407
select * from CIMS.Customer.ClaimBlackList WHERE CO_ContractID=407