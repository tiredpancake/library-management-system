import { useEffect, useState } from "react";

import { Plus } from "lucide-react";

import { getMembers, createMember } from "../api/memberApi";

import MemberFormModal from "../components/MemberFormModal";

function Members() {
  const [members, setMembers] = useState([]);

  const [open, setOpen] = useState(false);

  const load = () => {
    getMembers().then((res) => {
      setMembers(res.data);
    });
  };

  useEffect(() => {
    load();
  }, []);

  const save = (data) => {
    createMember(data).then(() => {
      load();

      setOpen(false);
    });
  };

  return (
    <div>
      <div
        className="
flex
justify-between
mb-6
"
      >
        <h1 className="text-2xl font-bold">Members</h1>

        <button
          onClick={() => setOpen(true)}
          className="
bg-blue-600
text-white
px-4
py-2
rounded-lg
flex
gap-2
"
        >
          <Plus size={18} />
          Add Member
        </button>
      </div>

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

              <th>National Code</th>

              <th>Phone</th>

              <th>Type</th>

              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {members.map((member) => (
              <tr key={member.id} className="border-b">
                <td className="p-3">{member.fullName}</td>

                <td>{member.nationalCode}</td>

                <td>{member.phone}</td>

                <td>{member.membershipType}</td>

                <td>{member.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <MemberFormModal
        open={open}
        onClose={() => setOpen(false)}
        onSubmit={save}
      />
    </div>
  );
}

export default Members;
