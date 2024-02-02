-- Table: public.email

-- DROP TABLE IF EXISTS public.email;

CREATE TABLE IF NOT EXISTS public.email
(
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    content text COLLATE pg_catalog."default",
    email_from character varying(255) COLLATE pg_catalog."default",
    email_sending_date timestamp(6) without time zone,
    email_to character varying(255) COLLATE pg_catalog."default",
    status character varying(255) COLLATE pg_catalog."default",
    subject character varying(255) COLLATE pg_catalog."default",
    verification character varying(255) COLLATE pg_catalog."default",
    user_id uuid,
    CONSTRAINT email_pkey PRIMARY KEY (id),
    CONSTRAINT fkah6v1juek8jb9ycg8cldv15d6 FOREIGN KEY (user_id)
    REFERENCES public.users (id) MATCH SIMPLE
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
    CONSTRAINT email_status_check CHECK (status::text = ANY (ARRAY['SENT'::character varying, 'ERROR'::character varying]::text[])),
    CONSTRAINT email_verification_check CHECK (verification::text = ANY (ARRAY['VERIFIED'::character varying, 'NON_VERIFIED'::character varying, 'UNVERIFIABLE'::character varying]::text[]))
    )

--     TABLESPACE pg_default;
--
-- ALTER TABLE IF EXISTS public.email
--     OWNER to postgres;