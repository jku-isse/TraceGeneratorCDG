INSERT INTO personnel(
MID,
AMID,
role,
lastName, 
firstName, 
address1,
address2,
city,
state,
zip,
phone,
specialty,
email)
VALUES (
9000000004,
null,
'hcp',
'Medico',
'Antonio',
'4321 My Road St',
'PO BOX 2',
'Vandelay City',
'NY',
'12345-1234',
'999-888-7777',
'surgeon',
'amedico@iTrust.org'
)ON DUPLICATE KEY UPDATE MID = MID;

INSERT INTO users(MID, password, role, sQuestion, sAnswer) VALUES(9000000004, '1a91d62f7ca67399625a4368a6ab5d4a3baa6073', 'hcp', 'first letter?', 'a')
ON DUPLICATE KEY UPDATE MID = MID;
/*password: pw*/
INSERT INTO hcpassignedhos(HCPID, HosID) VALUES(9000000004,'9191919191'), (9000000004,'8181818181')
ON DUPLICATE KEY UPDATE HCPID = HCPID;