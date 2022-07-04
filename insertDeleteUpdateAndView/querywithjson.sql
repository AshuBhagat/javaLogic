/*/JSON query return json OBJECT*/
SET SESSION group_concat_max_len = 1000000
SET GLOBAL group_concat_max_len = 1000000
SELECT tab1.expiryTabRs,flup.fowupTabRs,cli.clientid,cli.`firstname`,emp.`employeefirstname` FROM clienttable cli 
JOIN employeetable emp ON emp.`employeeid`=cli.employeeid
LEFT JOIN (SELECT CONCAT('{"+"''myExpDetailsArray''"+":[',GROUP_CONCAT(JSON_OBJECT('serialNumber',tab.serialnumber,'clientId',tab.clientid,'packageId',
tab.packageid,'activatedDate',tab.activateddate,'expiryDate',tab.expirydate,'discountPrice',
IF(tab.discountPrice IS NOT NULL,tab.discountPrice,0),'billNumberId',IF(tab.billnumberid!=NULL,tab.billnumberid,''))),']}') AS expiryTabRs,tab.clientid 
FROM (SELECT cliExp.serialnumber,cliExp.clientid,cliExp.packageid,cliExp.activateddate,cliExp.expirydate,cliExp.discountPrice,
cliExp.billnumberid FROM clientexpirypackagedetailtable cliExp WHERE cliExp.`clientid`="14311") AS tab) AS tab1 ON tab1.clientid=cli.clientid 

LEFT JOIN(SELECT CONCAT('{"+"''followupArray''"+":[',GROUP_CONCAT(JSON_OBJECT('fId',fow.followupid,'fupDate',fow.followupdate,'fTime',TIME_FORMAT(fow.followuptime,'%H:%i:%s'),'nextFupDate',fow.nextfollowupdate,'fByEmpId',fow.followupbyemployeeid,'fStatus',fow.followupstatus,'fComment',fow.followupcomment,'fEmpFLName',CONCAT(fow.employeefirstname,' ',fow.employeelastname))ORDER BY fow.followupId DESC),']}') AS fowupTabRs,fow.enquiryidorclientid 
FROM (SELECT f.*,emp.`employeefirstname`,emp.`employeelastname` FROM followuptable f JOIN employeetable emp ON emp.`employeeid`=f.`followupbyemployeeid`  
WHERE enquiryidorclientid = "14311" ORDER BY followupid DESC) AS fow) AS flup ON flup.enquiryidorclientid=cli.clientid 


WHERE cli.clientid="14311"

{"+"'myExpDetailsArray'"+":[{"clientId": 14311, "packageId": 45, "expiryDate": "2022-07-23", "billNumberId": "", "serialNumber": 727, 
"activatedDate": "2023-07-23", "discountPrice": 5000}]}