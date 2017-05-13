CREATE TABLE sys_user (
  id         SERIAL PRIMARY KEY,
  phone      BIGINT       NOT NULL,
  name       VARCHAR(120) NOT NULL,
  trustRate  BIGINT       NOT NULL,
  password   VARCHAR,
  registered BOOLEAN      NOT NULL
);

CREATE TABLE advert (
  id                 SERIAL PRIMARY KEY,
  publicationDate    BIGINT           NOT NULL,
  district           VARCHAR(3)       NOT NULL,
  address            VARCHAR(250)     NOT NULL,
  floor              INT              NOT NULL,
  maxFloor           INT              NOT NULL,
  rooms              INT              NOT NULL,
  sq                 INT              NOT NULL,
  price              INT              NOT NULL,
  withPublicServices BOOLEAN          NOT NULL,
  conditions         INT              NOT NULL,
  description        VARCHAR(1500)    NOT NULL,
  raw                BOOLEAN          NOT NULL,
  latitude           DOUBLE PRECISION NOT NULL,
  longitude          DOUBLE PRECISION NOT NULL,
  trustRate          BIGINT           NOT NULL
);

CREATE TABLE advert_author (
  isMain   BOOLEAN NOT NULL,
  advertId BIGINT  NOT NULL,
  userId   BIGINT  NOT NULL
);

CREATE TABLE photo (
  id       SERIAL PRIMARY KEY,
  advertId BIGINT  NOT NULL,
  path     VARCHAR NOT NULL,
  main     BOOLEAN NOT NULL,
  hash     BIGINT  NOT NULL,
  FOREIGN KEY (advertId) REFERENCES advert (id)
);

CREATE TABLE importState (
  typeName       VARCHAR(100) PRIMARY KEY,
  lastImportDate BIGINT NOT NULL
);

INSERT INTO importState (typeName, lastImportDate) VALUES ('AVT', 0)