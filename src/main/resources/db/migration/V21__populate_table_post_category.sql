INSERT INTO public.post_category (post_id, category_id)
SELECT
    post_id,
    category_id
FROM (
         SELECT id AS post_id FROM public.post ORDER BY RANDOM() LIMIT 100
     ) AS p,
     (
         SELECT id AS category_id FROM public.category ORDER BY RANDOM() LIMIT 2
     ) AS c;