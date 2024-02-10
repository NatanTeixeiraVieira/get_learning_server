-- Table: public.post

-- DROP TABLE IF EXISTS public.post;

CREATE TABLE IF NOT EXISTS public.post
(
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    allow_comments boolean,
    content character varying(255) COLLATE pg_catalog."default",
    post_time timestamp(6) without time zone,
    subtitle character varying(255) COLLATE pg_catalog."default",
    title character varying(255) COLLATE pg_catalog."default",
    author_id uuid,
    cover_image_id uuid,
    CONSTRAINT post_pkey PRIMARY KEY (id),
    CONSTRAINT uk_4myhtiddgqubbsy8cb6on3qif UNIQUE (cover_image_id),
    CONSTRAINT fk5l759v7apba3lqguc7bp8h456 FOREIGN KEY (author_id)
    REFERENCES public.author (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
    CONSTRAINT fk6vg5yjtvxm32jf1xagenxgvbx FOREIGN KEY (cover_image_id)
    REFERENCES public.cover_image (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
    )

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.post
--     OWNER to postgres;