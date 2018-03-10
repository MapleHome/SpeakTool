package com.speektool.injectmodules;

import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import roboguice.inject.InjectView;
import roboguice.inject.Nullable;

public class ViewInjectUtils {

    public static void injectViews(final ViewGroup viewGroup) {
        // setlayout========================================.
        final Class<? extends ViewGroup> cls = viewGroup.getClass();
        boolean isHaveLayout = cls.isAnnotationPresent(Layout.class);
        if (isHaveLayout) {
            Layout layout = cls.getAnnotation(Layout.class);
            int id = layout.value();
            View.inflate(viewGroup.getContext(), id, viewGroup);

        }

        // setfiled=============================================================.
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectView.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new UnsupportedOperationException("Views can't be staticaly assigned.");
                } else {
                    if (View.class.isAssignableFrom(field.getType())) {
                        try {
                            final InjectView injectView = field.getAnnotation(InjectView.class);
                            final int id = injectView.value();
                            View view = viewGroup.findViewById(id);
                            if ((view == null) && Nullable.notNullable(field)) {
                                throw new NullPointerException(String.format(
                                        "Can't inject null value into %s.%s when field is not @Nullable",
                                        field.getDeclaringClass(), field.getName()));
                            }
                            field.setAccessible(true);
                            field.set(viewGroup, view);
                        } catch (IllegalAccessException e) {
                            throw new IllegalStateException(e);
                        }
                    } else {
                        throw new UnsupportedOperationException("Need view type to assign");
                    }
                }
            }
        }

    }
}
