---
layout: default
title: Information for Course Staff
---

This page has links to all course content, but is not shared with the students
in the class. It is not a big deal if students see this page, but keeping it
confidential will give us more flexibility.

# Staff Roles

## Piazza duty

Spend up to 1 hour per day. You're encouraged to dedicate a block of time,
instead of checking it continuously. (E.g., pick a time when you aren't very
productive with other things.)

- Sunday:
- Monday:
- Tuesday:
- Wednesday:
- Thursday:
- Friday:

## Office hours

Office hour slots are for 1 hour. **Do not stay longer. Do not hold extra
hours.**

- Slot 1: Arjun (date and time flexible)
- Slot 2:
- Slot 3:
- Slot 4:
- Slot 5:

## Discussions

There are four discussion slots each week (i.e., 4 hours total).

- Wednesday, Jan 27. Topic: Help students with Homework 1.
- Wednesday, Feb 3. **Instructor: Dan.**
- Wednesday, Feb 10.
- Wednesday, Feb 17. **Instructor: Dan.**
- Wednesday, Feb 24.
- Wednesday, Mar 2. **Instructor: Dan.**
- Wednesday, Mar 9. **Instructor: Dan.**
- Wednesday, Mar 23.
- Wednesday, Mar 30. **Instructor: Dan.**
- Wednesday, Apr 6.
- Wednesday, Apr 13. **Instructor: Dan.**
- Wednesday, Apr 27. Topic: Help students with Homework 12. **Instructor: Dan.**

Lecture Notes
-------------

<ul>
{% for item in site.data.reading %}

  <li><a href="../reading/{{ item.path }}">{{ item.name }}</a></li>

{% endfor %}
</ul>


Homework
--------

<ul>
  {% for post in site.data.hw %}
    <li>
      <a href="../hw/{{ post.url }}">{{ post.title }}</a>
    </li>
  {% endfor %}
</ul>
