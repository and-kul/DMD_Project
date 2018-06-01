DROP INDEX users_articles_user_id_index;
DROP INDEX users_articles_article_id_index;
DROP TABLE users_articles;
DROP INDEX users_dict_login_index;
DROP TABLE users_dict;
DROP INDEX affiliations_articles_affil_id_index;
DROP INDEX affiliations_articles_article_id_index;
DROP TABLE affiliations_articles;
DROP INDEX affiliations_dict_search_index;
DROP TRIGGER affiliations_dict_tsv_update ON affiliations_dict;
DROP TABLE affiliations_dict;
DROP INDEX categories_articles_category_id_index;
DROP INDEX categories_articles_article_id_index;
DROP TABLE categories_articles;
DROP TABLE categories_dict;
DROP INDEX authors_articles_author_id_index;
DROP INDEX authors_articles_article_id_index;
DROP TABLE authors_articles;
DROP INDEX authors_dict_lower_author_name_index;
DROP TABLE authors_dict;
DROP INDEX main_search_index;
DROP TRIGGER main_tsv_update ON main;
DROP INDEX main_published_index;
DROP TABLE main;

CREATE TABLE main (
  id          SERIAL PRIMARY KEY,
  title       TEXT NOT NULL,
  published   DATE NOT NULL,
  summary     TEXT NOT NULL,
  comment     TEXT,
  doi         TEXT,
  pdf         TEXT,
  journal_ref TEXT,
  tsv         TSVECTOR
);

CREATE INDEX main_published_index ON main (published);

CREATE TRIGGER main_tsv_update BEFORE INSERT OR UPDATE
ON main FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(tsv, 'pg_catalog.english', title);

CREATE INDEX main_search_index ON main USING GIN (tsv);


CREATE TABLE authors_dict (
  author_id   SERIAL PRIMARY KEY,
  author_name TEXT NOT NULL
);

CREATE INDEX authors_dict_lower_author_name_index ON authors_dict (lower(author_name));

CREATE TABLE authors_articles (
  article_id INTEGER NOT NULL REFERENCES main (id) ON DELETE CASCADE,
  author_id  INTEGER NOT NULL REFERENCES authors_dict (author_id)
);

CREATE INDEX authors_articles_article_id_index ON authors_articles (article_id);
CREATE INDEX authors_articles_author_id_index ON authors_articles (author_id);


CREATE TABLE categories_dict (
  category_id   SERIAL PRIMARY KEY,
  category_name TEXT NOT NULL UNIQUE
);

CREATE TABLE categories_articles (
  article_id  INTEGER NOT NULL REFERENCES main (id) ON DELETE CASCADE,
  category_id INTEGER NOT NULL REFERENCES categories_dict (category_id)
);

CREATE INDEX categories_articles_article_id_index ON categories_articles (article_id);
CREATE INDEX categories_articles_category_id_index ON categories_articles (category_id);


CREATE TABLE affiliations_dict (
  affil_id   SERIAL PRIMARY KEY,
  affil_name TEXT NOT NULL,
  tsv        TSVECTOR
);

CREATE TRIGGER affiliations_dict_tsv_update BEFORE INSERT OR UPDATE
ON affiliations_dict FOR EACH ROW EXECUTE PROCEDURE
  tsvector_update_trigger(tsv, 'pg_catalog.english', affil_name);

CREATE INDEX affiliations_dict_search_index ON affiliations_dict USING GIN (tsv);


CREATE TABLE affiliations_articles (
  article_id INTEGER NOT NULL REFERENCES main (id) ON DELETE CASCADE,
  affil_id   INTEGER NOT NULL REFERENCES affiliations_dict (affil_id)
);

CREATE INDEX affiliations_articles_article_id_index ON affiliations_articles (article_id);
CREATE INDEX affiliations_articles_affil_id_index ON affiliations_articles (affil_id);


CREATE TABLE users_dict (
  user_id SERIAL PRIMARY KEY,
  login   TEXT UNIQUE NOT NULL,
  hash    TEXT NOT NULL
);

CREATE INDEX users_dict_login_index ON users_dict (login);

CREATE TABLE users_articles (
  article_id INTEGER NOT NULL REFERENCES main (id) ON DELETE CASCADE,
  user_id    INTEGER NOT NULL REFERENCES users_dict (user_id)
);

CREATE INDEX users_articles_article_id_index ON users_articles (article_id);
CREATE INDEX users_articles_user_id_index ON users_articles (user_id);


INSERT INTO categories_dict (category_id, category_name) VALUES
  (1, 'Physics'),
  (2, 'Mathematics'),
  (3, 'Computer Science'),
  (4, 'Quantitative Biology'),
  (5, 'Quantitative Finance'),
  (6, 'Statistics');

INSERT INTO users_dict (login, hash) VALUES
  ('root', 'b4b8daf4b8ea9d39568719e1e320076f');
/* rootroot */
