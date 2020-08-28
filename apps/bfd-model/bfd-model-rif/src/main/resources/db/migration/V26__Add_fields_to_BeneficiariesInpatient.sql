/*
 * The column doesn't have a default value to avoid updating the column on migration. The pipeline server
 * will populate the column as new beneficaries are added or existing beneficaries are updated. 
 */

alter table "Beneficiaries" add column "derivedMailingAddress1" varchar(40);
alter table "Beneficiaries" add column "derivedMailingAddress2" varchar(40);
alter table "Beneficiaries" add column "derivedMailingAddress3" varchar(40);
alter table "Beneficiaries" add column "derivedMailingAddress4" varchar(40);
alter table "Beneficiaries" add column "derivedMailingAddress5" varchar(40);
alter table "Beneficiaries" add column "derivedMailingAddress6" varchar(40);
alter table "Beneficiaries" add column "derivedCityName" varchar(100);
alter table "Beneficiaries" add column "derivedStateCode" varchar(2);
alter table "Beneficiaries" add column "derivedZipCode" varchar(9);
alter table "Beneficiaries" add column "mbiEffectiveDate" date;
alter table "Beneficiaries" add column "mbiObsoleteDate" date;
alter table "Beneficiaries" add column "beneLinkKey" numeric(38);

alter table "BeneficiariesHistory" add column "mbiEffectiveDate" date;
alter table "BeneficiariesHistory" add column "mbiObsoleteDate" date;

alter table "InpatientClaimLines" add column "clmUncompensatedCareAmount" numeric(38, 2);

alter table "CarrierClaimLines" add column "clmControlNumber" varchar(23) not null;

alter table "DMEClaimLines" add column "clmControlNumber" varchar(23) not null;

alter table "HHAClaimLines" add column "fiDocumentClaimControlNumber" varchar(23) not null;
alter table "HHAClaimLines" add column "fiOriginalClaimControlNumber" varchar(23) not null;

alter table "HospiceClaimLines" add column "fiDocumentClaimControlNumber" varchar(23) not null;
alter table "HospiceClaimLines" add column "fiOriginalClaimControlNumber" varchar(23) not null;

alter table "InpatientClaimLines" add column "fiDocumentClaimControlNumber" varchar(23) not null;
alter table "InpatientClaimLines" add column "fiOriginalClaimControlNumber" varchar(23) not null;

alter table "OutpatientClaimLines" add column "fiDocumentClaimControlNumber" varchar(23) not null;
alter table "OutpatientClaimLines" add column "fiOriginalClaimControlNumber" varchar(23) not null;

alter table "SNFClaimLines" add column "fiDocumentClaimControlNumber" varchar(23) not null;
alter table "SNFClaimLines" add column "fiOriginalClaimControlNumber" varchar(23) not null;