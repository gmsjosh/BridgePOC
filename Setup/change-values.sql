use CIMS
UPDATE CIMS.Customer.ClaimBlackList SET IsIndividual=(CASE IsIndividual WHEN 1 THEN 0 ELSE 1 END) WHERE CO_ContractID=407
UPDATE CIMS.Financial.ClaimCostPlus SET CCP_AdminFee=(CASE CCP_AdminFee WHEN 49.71 THEN 50.00 ELSE 49.71 END) WHERE CL_ClaimID=16937635
select * from CIMS.Customer.ClaimBlackList WHERE CO_ContractID=407
select * from CIMS.Financial.ClaimCostPlus WHERE CL_ClaimID=16937635

select * from Financial.ClaimVoucherNoteClaimLink
select * from Financial.ClaimNoteLink

update CIMS.Financial.ClaimStatus set CS_Description='joe   man' where CS_ClaimStatusID=1298739
select * from Cims.Financial.ClaimStatus where CS_ClaimStatusID=1298739
update CIMS.Financial.ClaimVoucherNoteClaimLink set VN_VoucherNoteID=5 where CL_ClaimID=21536675
select * from CIMS.Financial.ClaimVoucherNoteClaimLink where CL_ClaimID=21536675
update CIMS.Financial.ClaimNoteLink set NT_NoteID=1234 where CL_ClaimID=21536675
select * from CIMS.Financial.ClaimNoteLink where CL_ClaimID=21536675