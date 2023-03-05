-- get profit by department where profit more than 1000

select d.name, sum(amount) as sum_amount  from sales
    join departments d on d.id = sales.department_id
                                                 group by d.name
                                                 having sum(amount) >= 150;

select distinct u.name,
       d.name,
       sum(amount) over (partition by (department_id, user_id)) as amount
       from sales
           inner join departments d on d.id = sales.department_id
           inner join users u on u.id = sales.user_id;