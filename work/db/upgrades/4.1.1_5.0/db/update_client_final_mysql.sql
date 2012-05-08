ALTER TABLE KRNS_SESN_DOC_T ADD (OBJ_ID VARCHAR(36));
ALTER TABLE KRNS_SESN_DOC_T ADD (VER_NBR DECIMAL(8) DEFAULT 0);
ALTER TABLE KRSB_MSG_QUE_T CHANGE SVC_NMSPC APPL_ID VARCHAR(255);
ALTER TABLE KRNS_DOC_HDR_T MODIFY FDOC_DESC varchar(255);
ALTER TABLE KRNS_ATT_T CHANGE MIME_TYP VARCHAR(255);
ALTER TABLE KRNS_MAINT_DOC_ATT_T CHANGE CNTNT_TYP VARCHAR(255);
RENAME TABLE KRNS_MAINT_LOCK_T TO KRNS_MAINT_LOCK_T_BKUP;
ALTER TABLE KRNS_MAINT_LOCK_T_BKUP DROP PRIMARY KEY;
ALTER TABLE KRNS_MAINT_LOCK_T_BKUP DROP INDEX KRNS_MAINT_LOCK_TC1;
ALTER TABLE KRNS_MAINT_LOCK_T_BKUP DROP INDEX KRNS_MAINT_LOCK_TI2;
CREATE TABLE KRNS_MAINT_LOCK_T  ( 
	MAINT_LOCK_REP_TXT	varchar(500) NOT NULL,
	OBJ_ID            	varchar(36) NOT NULL,
	VER_NBR           	decimal(8,0) NOT NULL DEFAULT '1',
	DOC_HDR_ID        	varchar(14) NOT NULL,
	MAINT_LOCK_ID     	varchar(14),
	PRIMARY KEY(MAINT_LOCK_ID)
);

ALTER TABLE KRNS_MAINT_LOCK_T ADD CONSTRAINT KRNS_MAINT_LOCK_TC0 UNIQUE (OBJ_ID);

CREATE INDEX KRNS_MAINT_LOCK_TI2 ON KRNS_MAINT_LOCK_T(DOC_HDR_ID);

INSERT INTO KRNS_MAINT_LOCK_T(MAINT_LOCK_ID, OBJ_ID, VER_NBR, DOC_HDR_ID, MAINT_LOCK_REP_TXT) 
    (SELECT MAINT_LOCK_ID, OBJ_ID, VER_NBR, DOC_HDR_ID, MAINT_LOCK_REP_TXT 
        FROM KRNS_MAINT_LOCK_T_BKUP);
COMMIT;

