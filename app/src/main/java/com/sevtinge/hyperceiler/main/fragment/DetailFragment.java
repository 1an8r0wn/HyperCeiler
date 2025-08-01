/*
 * This file is part of HyperCeiler.

 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2025 HyperCeiler Contributions
 */
package com.sevtinge.hyperceiler.main.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fan.appcompat.app.Fragment;

public class DetailFragment extends Fragment {

    String mFragmentName;

    View mEmptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThemeRes(com.sevtinge.hyperceiler.ui.R.style.NavigatorSecondaryContentTheme);
    }

    @Override
    public View onInflateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.sevtinge.hyperceiler.ui.R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewInflated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewInflated(view, savedInstanceState);
        mEmptyView = view.findViewById(com.sevtinge.hyperceiler.ui.R.id.empty);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    @Override
    public void onUpdateArguments(Bundle args) {
        super.onUpdateArguments(args);
        if (args != null) {
            mFragmentName = args.getString("FragmentName");
            //int fragmentResId = args.getInt(":settings:fragment_resId");

            if (!TextUtils.isEmpty(mFragmentName)) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(com.sevtinge.hyperceiler.ui.R.id.frame_content, androidx.fragment.app.Fragment.instantiate(requireContext(), mFragmentName, args))
                        .commit();

                mEmptyView.setVisibility(View.INVISIBLE);

            }
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }
}
