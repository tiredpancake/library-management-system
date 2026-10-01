import { useEffect, useState } from "react";

import { Eye, Pencil, Plus, Search, X } from "lucide-react";

import {
  getMembers,
  getMemberByMembershipNumber,
  getMemberByNationalCode,
  createMember,
  updateMember,
} from "../api/memberApi";

import MemberFormModal from "../components/MemberFormModal";
import MemberViewModal from "../components/MemberViewModal";

function getErrorMessage(error, fallback) {
  return (
    error?.response?.data?.message || error?.response?.data?.error || fallback
  );
}

function Members() {
  const [members, setMembers] = useState([]);

  const [open, setOpen] = useState(false);

  const [editMember, setEditMember] = useState(null);

  const [viewMember, setViewMember] = useState(null);

  const [searchMembershipNumber, setSearchMembershipNumber] = useState("");

  const [searchNationalCode, setSearchNationalCode] = useState("");

  const [searchError, setSearchError] = useState("");

  const [searching, setSearching] = useState(false);

  const load = () => {
    getMembers()
      .then((res) => {
        setMembers(res.data);
      })
      .catch((error) => {
        setSearchError(getErrorMessage(error, "Failed to load members."));
      });
  };

  useEffect(() => {
    load();
  }, []);

  const openCreateMember = () => {
    setEditMember(null);
    setOpen(true);
  };

  const openEditMember = (member) => {
    setEditMember(member);
    setOpen(true);
  };

  const closeForm = () => {
    setOpen(false);
    setEditMember(null);
  };

  const save = async (data) => {
    if (editMember) {
      const response = await updateMember(editMember.id, data);

      const updatedMember = response.data;

      setMembers((currentMembers) =>
        currentMembers.map((member) =>
          member.id === updatedMember.id ? updatedMember : member,
        ),
      );

      setViewMember((currentMember) =>
        currentMember?.id === updatedMember.id ? updatedMember : currentMember,
      );

      await load();

      closeForm();

      return;
    }

    await createMember(data);

    await load();

    closeForm();
  };

  const searchMember = async (type) => {
    const value =
      type === "membership"
        ? searchMembershipNumber.trim()
        : searchNationalCode.trim();

    if (!value) {
      setSearchError(
        type === "membership"
          ? "Enter a membership number."
          : "Enter a national code.",
      );

      return;
    }

    setSearching(true);

    setSearchError("");

    try {
      const response =
        type === "membership"
          ? await getMemberByMembershipNumber(value)
          : await getMemberByNationalCode(value);

      setViewMember(response.data);
    } catch (error) {
      setSearchError(getErrorMessage(error, "Member not found."));
    } finally {
      setSearching(false);
    }
  };

  const clearSearch = () => {
    setSearchMembershipNumber("");

    setSearchNationalCode("");

    setSearchError("");

    load();
  };

  return (
    <div>
      {/* Header */}

      <div className="flex justify-between mb-6">
        <h1 className="text-2xl font-bold">Members</h1>

        <button
          type="button"
          onClick={openCreateMember}
          className="
          bg-blue-600
          text-white
          px-4
          py-2
          rounded-lg
          flex
          items-center
          gap-2
          "
        >
          <Plus size={18} />
          Add Member
        </button>
      </div>

      {/* Search */}

      <div
        className="
        bg-white
        rounded-xl
        shadow
        p-5
        mb-6
        "
      >
        <div className="flex items-center gap-2 mb-4">
          <Search size={19} />

          <h2 className="font-semibold">Find Member</h2>
        </div>

        <div
          className="
          grid
          grid-cols-1
          md:grid-cols-2
          gap-4
          "
        >
          {/* Membership Number Search */}

          <div>
            <label className="block text-sm font-medium mb-1">
              Membership Number
            </label>

            <div className="flex gap-2">
              <input
                value={searchMembershipNumber}
                onChange={(e) => {
                  setSearchMembershipNumber(e.target.value);
                  setSearchError("");
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    searchMember("membership");
                  }
                }}
                placeholder="Enter membership number"
                className="
                w-full
                border
                rounded-lg
                px-3
                py-2
                "
              />

              <button
                type="button"
                onClick={() => searchMember("membership")}
                disabled={searching}
                className="
                bg-blue-600
                text-white
                px-4
                py-2
                rounded-lg
                disabled:opacity-50
                "
              >
                Search
              </button>
            </div>
          </div>

          {/* National Code Search */}

          <div>
            <label className="block text-sm font-medium mb-1">
              National Code
            </label>

            <div className="flex gap-2">
              <input
                value={searchNationalCode}
                onChange={(e) => {
                  setSearchNationalCode(e.target.value);
                  setSearchError("");
                }}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    searchMember("national");
                  }
                }}
                placeholder="Enter national code"
                className="
                w-full
                border
                rounded-lg
                px-3
                py-2
                "
              />

              <button
                type="button"
                onClick={() => searchMember("national")}
                disabled={searching}
                className="
                bg-blue-600
                text-white
                px-4
                py-2
                rounded-lg
                disabled:opacity-50
                "
              >
                Search
              </button>
            </div>
          </div>
        </div>

        {/* Search Error */}

        {searchError && (
          <div className="mt-4 text-sm text-red-600">{searchError}</div>
        )}

        {/* Clear Search */}

        {(searchMembershipNumber || searchNationalCode) && (
          <button
            type="button"
            onClick={clearSearch}
            className="
            mt-4
            flex
            items-center
            gap-1
            text-sm
            text-gray-600
            hover:text-gray-900
            "
          >
            <X size={15} />
            Clear search
          </button>
        )}
      </div>

      {/* Members Table */}

      <div
        className="
        bg-white
        rounded-xl
        shadow
        p-5
        overflow-x-auto
        "
      >
        <table className="w-full">
          <thead>
            <tr className="border-b text-left">
              <th className="p-3">Name</th>

              <th>Membership Number</th>

              <th>National Code</th>

              <th>Phone</th>

              <th>Type</th>

              <th>Status</th>

              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {members.map((member) => (
              <tr key={member.id} className="border-b">
                <td className="p-3">{member.fullName}</td>

                <td>{member.membershipNumber}</td>

                <td>{member.nationalCode}</td>

                <td>{member.phone}</td>

                <td>{member.membershipType}</td>

                <td>{member.status}</td>

                <td>
                  <div
                    className="
                    flex
                    items-center
                    gap-4
                    "
                  >
                    {/* View */}

                    <button
                      type="button"
                      onClick={() => {
                        setViewMember(member);
                      }}
                      className="
                      flex
                      items-center
                      gap-1
                      text-blue-600
                      hover:text-blue-800
                      "
                    >
                      <Eye size={17} />
                      View
                    </button>

                    {/* Edit */}

                    <button
                      type="button"
                      onClick={() => {
                        openEditMember(member);
                      }}
                      className="
                      flex
                      items-center
                      gap-1
                      text-orange-600
                      hover:text-orange-800
                      "
                    >
                      <Pencil size={17} />
                      Edit
                    </button>
                  </div>
                </td>
              </tr>
            ))}

            {members.length === 0 && (
              <tr>
                <td
                  colSpan="7"
                  className="
                  text-center
                  py-8
                  text-gray-500
                  "
                >
                  No members found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Add / Edit Member Modal */}

      <MemberFormModal
        open={open}
        onClose={closeForm}
        onSubmit={save}
        member={editMember}
      />

      {/* View Member Modal */}

      <MemberViewModal
        open={Boolean(viewMember)}
        onClose={() => {
          setViewMember(null);
        }}
        member={viewMember}
      />
    </div>
  );
}

export default Members;
