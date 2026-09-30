import { useEffect, useState } from "react";
import { Plus } from "lucide-react";

import { getMembers, createMember } from "../api/memberApi";

import MemberFormModal from "../components/MemberFormModal";

function Members() {
  const [members, setMembers] = useState([]);

  const [open, setOpen] = useState(false);

  const [loading, setLoading] = useState(false);

  const load = async () => {
    try {
      setLoading(true);

      const res = await getMembers();

      setMembers(res.data);
    } catch (err) {
      console.log("Loading members failed:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const save = async (data) => {
    try {
      await createMember(data);

      await load();

      setOpen(false);
    } catch (err) {
      console.log("Creating member failed:", err);
    }
  };

  return (
    <div>
      <div
        className="
flex
justify-between
items-center
mb-6
"
      >
        <div>
          <h1 className="text-2xl font-bold">Members</h1>

          <p className="text-slate-500">Manage library members</p>
        </div>

        <button
          onClick={() => setOpen(true)}
          className="
bg-blue-600
hover:bg-blue-700
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

      <div
        className="
bg-white
rounded-xl
shadow
p-5
overflow-x-auto
"
      >
        {loading ? (
          <p>Loading members...</p>
        ) : (
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
        )}
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
