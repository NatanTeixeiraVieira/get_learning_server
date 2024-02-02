-- Table: public.post_tag

-- DROP TABLE IF EXISTS public.post_tag;

CREATE TABLE IF NOT EXISTS public.post_tag
(
    post_id uuid NOT NULL,
    tag_id uuid NOT NULL,
    CONSTRAINT fkac1wdchd2pnur3fl225obmlg0 FOREIGN KEY (tag_id)
    REFERENCES public.tag (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT fkc2auetuvsec0k566l0eyvr9cs FOREIGN KEY (post_id)
    REFERENCES public.post (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.post_tag
--     OWNER to postgres;