-- Table: public.users_role

-- DROP TABLE IF EXISTS public.users_role;

CREATE TABLE IF NOT EXISTS public.users_role
(
    user_id uuid NOT NULL,
    role_id uuid NOT NULL,
    CONSTRAINT fk3qjq7qsiigxa82jgk0i0wuq3g FOREIGN KEY (role_id)
    REFERENCES public.role (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT fkqpe36jsen4rslwfx5i6dj2fy8 FOREIGN KEY (user_id)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users_role
    OWNER to postgres;