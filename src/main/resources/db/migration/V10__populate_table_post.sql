-- Habilitar a extensão uuid-ossp
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Inserir 100 posts na tabela post com IDs e conteúdo HTML aleatórios
DO $$
DECLARE
post_id uuid;
    html_content text;
BEGIN
FOR i IN 1..100 LOOP
        post_id := uuid_generate_v4();
        html_content := '<p>Conteúdo HTML aleatório para o Post ' || i || '</p><div>' || md5(random()::text) || '</div>';

INSERT INTO public.post (id, created_at, updated_at,  allow_comments, content, subtitle, title)
VALUES (post_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE, html_content, 'Subtítulo do Post ' || i, 'Título do Post ' || i);
END LOOP;
END $$;
