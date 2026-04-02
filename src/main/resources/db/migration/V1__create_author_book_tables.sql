-- ============================================
-- AUTHOR（著者）
-- ============================================
CREATE TABLE author (
                        author_id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        birth_date DATE NOT NULL
);

-- ============================================
-- BOOK（書籍）
-- ============================================
CREATE TABLE book (
                      book_id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      price INTEGER NOT NULL CHECK (price >= 0),
                      publishing_status VARCHAR(50) NOT NULL,
                      published_date DATE
);

-- ============================================
-- BOOK_AUTHOR（多対多の中間テーブル）
-- ============================================
CREATE TABLE book_author (
                             book_id BIGINT NOT NULL,
                             author_id BIGINT NOT NULL,

                             PRIMARY KEY (book_id, author_id),

                             CONSTRAINT fk_book_author_book
                                 FOREIGN KEY (book_id)
                                     REFERENCES book(book_id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_book_author_author
                                 FOREIGN KEY (author_id)
                                     REFERENCES author(author_id)
                                     ON DELETE CASCADE
);
