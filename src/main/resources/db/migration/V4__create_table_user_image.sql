-- Table: public.user_image

-- DROP TABLE IF EXISTS public.user_image;

CREATE TABLE IF NOT EXISTS public.user_image
(
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    name character varying(255) COLLATE pg_catalog."default",
    url character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT user_image_pkey PRIMARY KEY (id)
    );

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.user_image
--     OWNER to postgres;