INSERT INTO REPORT(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME,REPORT_IDENTIFIER) VALUES(27,4,'Loan Status Report',NULL);

INSERT INTO REPORT_JASPER_MAP(REPORT_ID,REPORT_CATEGORY_ID,REPORT_NAME, REPORT_IDENTIFIER, REPORT_JASPER) VALUES (27,4,'Loan Status Report',NULL,NULL);

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 111 WHERE DATABASE_VERSION = 110;
