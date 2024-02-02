-- Table: public.post_category

-- DROP TABLE IF EXISTS public.post_category;

CREATE TABLE IF NOT EXISTS public.post_category
(
    post_id uuid NOT NULL,
    category_id uuid NOT NULL,
    CONSTRAINT fkqly0d5oc4npxdig2fjfoshhxg FOREIGN KEY (category_id)
    REFERENCES public.category (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT fkqr4dx4cx1lh4jfjchabytcakl FOREIGN KEY (post_id)
    REFERENCES public.post (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.post_category
--     OWNER to postgres;