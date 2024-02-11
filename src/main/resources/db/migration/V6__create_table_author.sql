-- Table: public.author

-- DROP TABLE IF EXISTS public.author;

CREATE TABLE IF NOT EXISTS public.author
(
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    name character varying(255) COLLATE pg_catalog."default",
    slug character varying(255) COLLATE pg_catalog."default",
    author_image_id uuid,
    user_id uuid,
    CONSTRAINT author_pkey PRIMARY KEY (id),
    CONSTRAINT uk_dpyjaivxjf1ms3d3e0uv1j8ty UNIQUE (user_id),
    CONSTRAINT uk_i952gpheq9tkul2jarbkuyph9 UNIQUE (author_image_id),
    CONSTRAINT fk2ahaa6b47k0wu7hkiu9shcdm3 FOREIGN KEY (author_image_id)
    REFERENCES public.author_image (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
    CONSTRAINT fkqhtybqur3lfhup9k7vnyo2g82 FOREIGN KEY (user_id)
    REFERENCES public.users (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
)

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.author
--     OWNER to postgres;